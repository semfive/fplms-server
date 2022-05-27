using System.Linq.Expressions;
using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class LecturerRepository : RepositoryBase<Lecturer>, ILecturerRepository
    {
        public LecturerRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateLecturer(Lecturer lecturer)
        {
            Create(lecturer);
        }

        public void DeleteLecturer(Lecturer lecturer)
        {
            Delete(lecturer);
        }

        public async Task<IEnumerable<Lecturer>> GetAllLecturersAsync()
        {
            return await FindAll().ToListAsync();
        }

        public async Task<Lecturer> GetLecturerByIdAsync(Guid lecturerId)
        {
            return await FindByCondition(lecturer => lecturer.Id.Equals(lecturerId)).FirstOrDefaultAsync();
        }

        public void UpdateLecturer(Lecturer lecturer)
        {
            Update(lecturer);
        }
    }
}