using System.Data;
using AutoMapper;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [ApiExplorerSettings(IgnoreApi = true)]
    [Route("api/discussion/students")]
    public class StudentsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public StudentsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        public async Task<IActionResult> GetAllStudents()
        {
            try
            {
                var students = await _repositoryWrapper.StudentRepository.GetAllStudentsAsync();
                var result = _mapper.Map<List<GetStudentDto>>(students);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateStudent(CreateStudentDto createStudentDto)
        {
            try
            {
                Student student = _mapper.Map<Student>(createStudentDto);
                student.Id = Guid.NewGuid();

                _repositoryWrapper.StudentRepository.Create(student);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (DataException)
            {
                Console.WriteLine("Unable to save data");
                return StatusCode(500, "Internal server error");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return StatusCode(500, "Internal server error");
            }

        }

    }
}