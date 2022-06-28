using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class StudentUpvoteRepository : RepositoryBase<StudentUpvote>, IStudentUpvoteRepository
    {
        public StudentUpvoteRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public async void CreateStudentUpvote(StudentUpvote studentUpvote)
        {
            Create(studentUpvote);
        }

        public void DeleteStudentUpvote(StudentUpvote studentUpvote)
        {
            Delete(studentUpvote);
        }

        public async Task<StudentUpvote> GetStudentUpvote(StudentUpvote dto)
        {
            return await FindByCondition(studentUpvote => studentUpvote.QuestionId == dto.QuestionId && studentUpvote.StudentId == dto.StudentId).FirstOrDefaultAsync();
        }

        public void UpdateStudentUpvote(StudentUpvote studentUpvote)
        {
            throw new NotImplementedException();
        }
    }
}